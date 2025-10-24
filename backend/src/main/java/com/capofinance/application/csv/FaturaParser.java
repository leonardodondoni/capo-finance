package com.capofinance.application.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for credit card bill CSV (fatura.csv)
 * Format: Data;Estabelecimento;Portador;Valor;Parcela
 */
@Component
public class FaturaParser {

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("dd/MM/yy")
    };

    public List<ParsedTransaction> parse(InputStream inputStream) throws IOException {
        List<ParsedTransaction> transactions = new ArrayList<>();

        CSVFormat format = CSVFormat.Builder.create()
                .setDelimiter(';')
                .setHeader("Data", "Estabelecimento", "Portador", "Valor", "Parcela")
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .build();

        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, format)) {

            for (CSVRecord record : csvParser) {
                try {
                    ParsedTransaction transaction = parseRecord(record);
                    transactions.add(transaction);
                } catch (Exception e) {
                    // Log error but continue processing
                    System.err.println("Error parsing record: " + record + " - " + e.getMessage());
                }
            }
        }

        return transactions;
    }

    private ParsedTransaction parseRecord(CSVRecord record) {
        String dataStr = record.get("Data");
        String estabelecimento = record.get("Estabelecimento");
        String portador = record.get("Portador");
        String valorStr = record.get("Valor");
        String parcela = record.get("Parcela");

        LocalDateTime transactionDate = parseDate(dataStr);
        BigDecimal amount = parseMoney(valorStr);

        // Detect person from portador (card holder)
        String detectedPerson = detectPerson(portador);

        return ParsedTransaction.builder()
                .transactionDate(transactionDate)
                .description(estabelecimento)
                .amount(amount.abs()) // Always positive, type is EXPENSE
                .installmentInfo(parcela.isEmpty() || parcela.equals("-") ? null : parcela)
                .cardHolder(portador)
                .detectedPersonName(detectedPerson)
                .transactionType("EXPENSE") // Credit card bills are always expenses
                .build();
    }

    private LocalDateTime parseDate(String dateStr) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDate date = LocalDate.parse(dateStr, formatter);
                // Set time to noon to avoid timezone issues
                return LocalDateTime.of(date, LocalTime.NOON);
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }
        throw new IllegalArgumentException("Unable to parse date: " + dateStr);
    }

    private BigDecimal parseMoney(String moneyStr) {
        // Remove currency symbols, spaces, and convert comma to dot
        String cleaned = moneyStr
                .replace("R$", "")
                .replace(" ", "")
                .replace(".", "")  // Remove thousands separator
                .replace(",", ".") // Replace decimal comma with dot
                .trim();

        // Handle negative values (like payment credits)
        return new BigDecimal(cleaned);
    }

    private String detectPerson(String portador) {
        String upper = portador.toUpperCase();

        if (upper.contains("GIOVANA") || upper.contains("DORNELES")) {
            return "Giovana";
        }

        if (upper.contains("LEONARDO") || upper.contains("SIQUEIRA")) {
            return "Leonardo";
        }

        // Default to Leonardo
        return "Leonardo";
    }
}

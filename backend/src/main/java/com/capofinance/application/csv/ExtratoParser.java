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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for bank statement CSV (extrato.csv)
 * Format: Data;Descricao;Valor;Saldo
 */
@Component
public class ExtratoParser {

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ofPattern("dd/MM/yy 'às' HH:mm:ss"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm:ss"),
        DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
    };

    public List<ParsedTransaction> parse(InputStream inputStream) throws IOException {
        List<ParsedTransaction> transactions = new ArrayList<>();

        CSVFormat format = CSVFormat.Builder.create()
                .setDelimiter(';')
                .setHeader("Data", "Descricao", "Valor", "Saldo")
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
        String descricao = record.get("Descricao");
        String valorStr = record.get("Valor");
        String saldoStr = record.get("Saldo");

        LocalDateTime transactionDate = parseDate(dataStr);
        BigDecimal amount = parseMoney(valorStr);
        BigDecimal balance = parseMoney(saldoStr);

        // Detect transaction type (income vs expense)
        String transactionType = amount.compareTo(BigDecimal.ZERO) >= 0 ? "INCOME" : "EXPENSE";

        // Detect person from description
        String detectedPerson = detectPerson(descricao);

        return ParsedTransaction.builder()
                .transactionDate(transactionDate)
                .description(descricao)
                .amount(amount.abs()) // Store as positive, type indicates direction
                .balanceAfter(balance)
                .detectedPersonName(detectedPerson)
                .transactionType(transactionType)
                .build();
    }

    private LocalDateTime parseDate(String dateStr) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateStr, formatter);
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

        return new BigDecimal(cleaned);
    }

    private String detectPerson(String description) {
        String lowerDesc = description.toLowerCase();

        // Check for specific keywords
        if (lowerDesc.contains("giovana")) {
            return "Giovana";
        }

        // Default to Leonardo
        return "Leonardo";
    }
}

export interface Transaction {
  id: number;
  accountId: number;
  transactionType: string;
  amount: number;
  description: string;
  date: string;
  createdAt: string;
}

export interface CreateTransactionDTO {
  accountId: number;
  transactionType: string;
  amount: number;
  description: string;
  date: string;
}

import { api } from './apiClient';
import { Transaction, CreateTransactionDTO } from '@/types/Transaction';

export const transactionsApi = {
  getAll: async (): Promise<Transaction[]> => {
    const { data } = await api.get<Transaction[]>('/transactions');
    return data;
  },

  getById: async (id: number): Promise<Transaction> => {
    const { data } = await api.get<Transaction>(`/transactions/${id}`);
    return data;
  },

  create: async (transaction: CreateTransactionDTO): Promise<Transaction> => {
    const { data } = await api.post<Transaction>('/transactions', transaction);
    return data;
  },

  update: async (id: number, transaction: Partial<CreateTransactionDTO>): Promise<Transaction> => {
    const { data } = await api.put<Transaction>(`/transactions/${id}`, transaction);
    return data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/transactions/${id}`);
  },
};

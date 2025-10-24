import { api } from './apiClient';
import { Account, CreateAccountDTO } from '@/types/Account';

export const accountsApi = {
  getAll: async (): Promise<Account[]> => {
    const { data } = await api.get<Account[]>('/accounts');
    return data;
  },

  getById: async (id: number): Promise<Account> => {
    const { data } = await api.get<Account>(`/accounts/${id}`);
    return data;
  },

  create: async (account: CreateAccountDTO): Promise<Account> => {
    const { data } = await api.post<Account>('/accounts', account);
    return data;
  },

  update: async (id: number, account: Partial<CreateAccountDTO>): Promise<Account> => {
    const { data } = await api.put<Account>(`/accounts/${id}`, account);
    return data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/accounts/${id}`);
  },
};

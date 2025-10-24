import { makeAutoObservable, runInAction } from 'mobx';
import { transactionsApi } from '@/api/transactionsApi';
import { accountsApi } from '@/api/accountsApi';
import { Transaction } from '@/types/Transaction';
import { Account } from '@/types/Account';

class DashboardSceneStore {
  transactions: Transaction[] = [];
  accounts: Account[] = [];
  loading = false;
  error: string | null = null;

  constructor() {
    makeAutoObservable(this);
  }

  async fetchDashboardData() {
    this.loading = true;
    this.error = null;

    try {
      const [transactionsData, accountsData] = await Promise.all([
        transactionsApi.getAll(),
        accountsApi.getAll(),
      ]);

      runInAction(() => {
        this.transactions = transactionsData;
        this.accounts = accountsData;
        this.loading = false;
      });
    } catch (error) {
      runInAction(() => {
        this.error = error instanceof Error ? error.message : 'Failed to fetch dashboard data';
        this.loading = false;
      });
    }
  }

  get totalBalance(): number {
    return this.accounts.reduce((sum, account) => sum + account.balance, 0);
  }

  get recentTransactions(): Transaction[] {
    return this.transactions.slice(0, 5);
  }
}

export const dashboardSceneStore = new DashboardSceneStore();

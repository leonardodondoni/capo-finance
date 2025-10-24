import { observer } from 'mobx-react-lite';
import { useEffect } from 'react';
import { dashboardSceneStore } from './index';
import { formatCurrency, formatDate } from '@/utils/formatters';

export const Dashboard = observer(() => {
  useEffect(() => {
    dashboardSceneStore.fetchDashboardData();
  }, []);

  if (dashboardSceneStore.loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-xl">Loading...</div>
      </div>
    );
  }

  if (dashboardSceneStore.error) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-xl text-red-500">Error: {dashboardSceneStore.error}</div>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Dashboard</h1>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
          <h2 className="text-sm font-medium text-gray-600 dark:text-gray-400 mb-2">
            Total Balance
          </h2>
          <p className="text-2xl font-bold">
            {formatCurrency(dashboardSceneStore.totalBalance)}
          </p>
        </div>

        <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
          <h2 className="text-sm font-medium text-gray-600 dark:text-gray-400 mb-2">
            Total Accounts
          </h2>
          <p className="text-2xl font-bold">{dashboardSceneStore.accounts.length}</p>
        </div>

        <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
          <h2 className="text-sm font-medium text-gray-600 dark:text-gray-400 mb-2">
            Total Transactions
          </h2>
          <p className="text-2xl font-bold">{dashboardSceneStore.transactions.length}</p>
        </div>
      </div>

      {/* Accounts List */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-6 mb-8">
        <h2 className="text-xl font-bold mb-4">Accounts</h2>
        {dashboardSceneStore.accounts.length === 0 ? (
          <p className="text-gray-500">No accounts found</p>
        ) : (
          <div className="space-y-3">
            {dashboardSceneStore.accounts.map((account) => (
              <div
                key={account.id}
                className="flex justify-between items-center p-4 bg-gray-50 dark:bg-gray-700 rounded"
              >
                <div>
                  <p className="font-medium">{account.name}</p>
                  <p className="text-sm text-gray-500">ID: {account.id}</p>
                </div>
                <p className="text-lg font-bold">{formatCurrency(account.balance)}</p>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Recent Transactions */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
        <h2 className="text-xl font-bold mb-4">Recent Transactions</h2>
        {dashboardSceneStore.recentTransactions.length === 0 ? (
          <p className="text-gray-500">No transactions found</p>
        ) : (
          <div className="space-y-3">
            {dashboardSceneStore.recentTransactions.map((transaction) => (
              <div
                key={transaction.id}
                className="flex justify-between items-center p-4 bg-gray-50 dark:bg-gray-700 rounded"
              >
                <div>
                  <p className="font-medium">{transaction.description}</p>
                  <p className="text-sm text-gray-500">
                    {transaction.transactionType} • {formatDate(transaction.date)}
                  </p>
                </div>
                <p className="text-lg font-bold">{formatCurrency(transaction.amount)}</p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
});

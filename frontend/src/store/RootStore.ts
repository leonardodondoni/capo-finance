import { makeAutoObservable } from 'mobx';

export class RootStore {
  // Add your stores here
  // Example: userStore: UserStore;
  // Example: transactionStore: TransactionStore;

  constructor() {
    makeAutoObservable(this);
    // Initialize stores
    // this.userStore = new UserStore(this);
    // this.transactionStore = new TransactionStore(this);
  }
}

export const rootStore = new RootStore();

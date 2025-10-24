export interface Account {
  id: number;
  name: string;
  balance: number;
  createdAt: string;
}

export interface CreateAccountDTO {
  name: string;
  balance?: number;
}

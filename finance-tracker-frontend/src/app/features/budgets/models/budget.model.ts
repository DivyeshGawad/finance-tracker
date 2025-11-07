export interface Budget {
  budgetId?: string;
  userId?: string;
  name?:string;
  categoryId?: string;
  note?:string;
  budgetAmount?: number;
  spendAmount?: number;
  startDate?: string;
  endDate?: string;
  createdAt?: string;
  updatedAt?: string;
  percentUsed?:number;
  status?: 'ON_TRACK' | 'NEARING_LIMIT' | 'EXCEEDED' | string;
}

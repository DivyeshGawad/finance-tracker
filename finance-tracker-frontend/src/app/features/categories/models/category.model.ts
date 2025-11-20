export interface Category {
  categoryId: string;
  userId?: string | null;
  name: string;
  type: 'INCOME' | 'EXPENSE';
  isDefault: boolean;
  createdAt?: string;
  updatedAt?: string;
}
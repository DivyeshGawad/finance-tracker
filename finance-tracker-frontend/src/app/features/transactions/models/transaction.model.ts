export interface Transaction {
    transactionId?: string;
    userId?: string;
    categoryId?: string;
    budgetId?: string,
    categoryName?: string;
    categoryType?: string;
    description?: string;
    amount?: number;
    transactionDate?: string;
    createdAt?: string;          // optional, can mock
    updatedAt?: string;          // optional, can mock
}
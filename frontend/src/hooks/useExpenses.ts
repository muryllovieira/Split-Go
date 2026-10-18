import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { expensesApi } from "../api/expenses";
import type { CreateExpenseInput } from "../api/types";

export function useExpenses(groupId: string) {
  const queryClient = useQueryClient();

  const list = useQuery({
    queryKey: ["expenses", groupId],
    queryFn: () => expensesApi.list(groupId),
  });

  const create = useMutation({
    mutationFn: (input: CreateExpenseInput) => expensesApi.create(groupId, input),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["expenses", groupId] });
      queryClient.invalidateQueries({ queryKey: ["settlement", groupId] });
    },
  });

  return { list, create };
}

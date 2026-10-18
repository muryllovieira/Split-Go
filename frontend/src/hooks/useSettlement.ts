import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { settlementApi } from "../api/expenses";
import type { CreatePaymentInput } from "../api/types";

export function useSettlement(groupId: string) {
  const queryClient = useQueryClient();

  const settlement = useQuery({
    queryKey: ["settlement", groupId],
    queryFn: () => settlementApi.get(groupId),
  });

  const payments = useQuery({
    queryKey: ["payments", groupId],
    queryFn: () => settlementApi.listPayments(groupId),
  });

  const pay = useMutation({
    mutationFn: (input: CreatePaymentInput) => settlementApi.createPayment(groupId, input),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["settlement", groupId] });
      queryClient.invalidateQueries({ queryKey: ["payments", groupId] });
    },
  });

  return { settlement, payments, pay };
}

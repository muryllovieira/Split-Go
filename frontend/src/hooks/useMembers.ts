import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { groupsApi } from "../api/groups";

export function useMembers(groupId: string) {
  const queryClient = useQueryClient();
  const refresh = () => {
    queryClient.invalidateQueries({ queryKey: ["members", groupId] });
    queryClient.invalidateQueries({ queryKey: ["settlement", groupId] });
  };

  const list = useQuery({
    queryKey: ["members", groupId],
    queryFn: () => groupsApi.listMembers(groupId),
  });

  const add = useMutation({
    mutationFn: (name: string) => groupsApi.addMember(groupId, name),
    onSuccess: refresh,
  });

  const remove = useMutation({
    mutationFn: (memberId: string) => groupsApi.removeMember(groupId, memberId),
    onSuccess: refresh,
  });

  return {
    list,
    add: (name: string) => {
      remove.reset();
      return add.mutateAsync(name);
    },
    remove: (memberId: string) => {
      add.reset();
      remove.mutate(memberId);
    },
    isAdding: add.isPending,
    isRemoving: remove.isPending,
    error: add.error ?? remove.error,
  };
}

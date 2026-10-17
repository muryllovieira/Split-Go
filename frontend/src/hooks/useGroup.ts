import { useEffect } from "react";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useNavigate } from "react-router";
import { groupsApi } from "../api/groups";
import { rememberGroup } from "../lib/recentGroups";

export function useGroup(groupId: string) {
  const query = useQuery({
    queryKey: ["group", groupId],
    queryFn: () => groupsApi.get(groupId),
  });

  useEffect(() => {
    if (query.data) {
      rememberGroup({ id: query.data.id, name: query.data.name });
    }
  }, [query.data]);

  return query;
}

export function useCreateGroup() {
  const navigate = useNavigate();

  return useMutation({
    mutationFn: groupsApi.create,
    onSuccess: (group) => {
      rememberGroup({ id: group.id, name: group.name });
      navigate(`/groups/${group.id}`);
    },
  });
}

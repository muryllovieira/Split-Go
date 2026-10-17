import { api } from "./client";
import type { CreateGroupInput, Group, Member } from "./types";

export const groupsApi = {
  create: (input: CreateGroupInput) =>
    api<Group>("/groups", { method: "POST", body: JSON.stringify(input) }),

  get: (groupId: string) => api<Group>(`/groups/${groupId}`),

  listMembers: (groupId: string) => api<Member[]>(`/groups/${groupId}/members`),

  addMember: (groupId: string, name: string) =>
    api<Member>(`/groups/${groupId}/members`, { method: "POST", body: JSON.stringify({ name }) }),

  removeMember: (groupId: string, memberId: string) =>
    api<void>(`/groups/${groupId}/members/${memberId}`, { method: "DELETE" }),
};

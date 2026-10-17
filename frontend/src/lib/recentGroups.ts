import type { Group } from "../api/types";

const STORAGE_KEY = "splitgo:recent-groups";
const MAX_RECENT = 5;

export type RecentGroup = Pick<Group, "id" | "name">;

export function getRecentGroups(): RecentGroup[] {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY) ?? "[]");
  } catch {
    return [];
  }
}

export function rememberGroup(group: RecentGroup) {
  try {
    const others = getRecentGroups().filter((g) => g.id !== group.id);
    localStorage.setItem(
      STORAGE_KEY,
      JSON.stringify([group, ...others].slice(0, MAX_RECENT)),
    );
  } catch {}
}

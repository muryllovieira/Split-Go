import { Link } from "react-router";
import type { RecentGroup } from "../../lib/recentGroups";

type Props = {
  groups: RecentGroup[];
};

export default function RecentGroupsList({ groups }: Props) {
  if (groups.length === 0) {
    return null;
  }

  return (
    <section>
      <h2 className="text-sm font-semibold uppercase tracking-wide text-slate-500">Seus grupos recentes</h2>
      <ul className="mt-2 divide-y divide-slate-200 rounded-xl border border-slate-200 bg-white">
        {groups.map((group) => (
          <li key={group.id}>
            <Link to={`/groups/${group.id}`} className="block px-4 py-3 hover:bg-slate-50">
              {group.name}
            </Link>
          </li>
        ))}
      </ul>
    </section>
  );
}

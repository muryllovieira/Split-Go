import { CreateGroupForm, RecentGroupsList } from "../components/organisms";
import { useCreateGroup } from "../hooks/useGroup";
import { getRecentGroups } from "../lib/recentGroups";

export default function HomePage() {
  const createGroup = useCreateGroup();

  return (
    <div className="space-y-8">
      <section>
        <h1 className="text-3xl font-bold tracking-tight">Divida despesas sem dor de cabeça</h1>
        <p className="mt-2 text-slate-600">
          Crie um grupo, lance os gastos e veja quem paga quem com o menor número de transferências.
        </p>
      </section>

      <CreateGroupForm
        onSubmit={(input) => createGroup.mutate(input)}
        isSubmitting={createGroup.isPending}
        errorMessage={createGroup.error?.message}
      />

      <RecentGroupsList groups={getRecentGroups()} />
    </div>
  );
}

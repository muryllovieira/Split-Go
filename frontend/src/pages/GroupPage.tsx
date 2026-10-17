import { useParams } from "react-router";
import { EmptyState } from "../components/molecules";
import { GroupHeader, MembersPanel } from "../components/organisms";
import { useGroup } from "../hooks/useGroup";
import { useMembers } from "../hooks/useMembers";

export default function GroupPage() {
  const { groupId = "" } = useParams();
  const groupQuery = useGroup(groupId);
  const members = useMembers(groupId);

  if (groupQuery.isPending) {
    return <p className="text-slate-500">Carregando grupo...</p>;
  }

  if (groupQuery.isError) {
    return (
      <EmptyState
        title="Grupo não encontrado"
        description={groupQuery.error.message}
        actionLabel="Criar um novo grupo"
        actionTo="/"
      />
    );
  }

  const group = groupQuery.data;

  return (
    <div className="space-y-6">
      <GroupHeader name={group.name} currency={group.baseCurrency} />
      <MembersPanel
        members={members.list.data}
        isLoading={members.list.isPending}
        onAdd={members.add}
        onRemove={members.remove}
        isAdding={members.isAdding}
        isRemoving={members.isRemoving}
        errorMessage={members.error?.message}
      />
    </div>
  );
}

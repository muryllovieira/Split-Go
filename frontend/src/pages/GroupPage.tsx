import { useState } from "react";
import { useParams } from "react-router";
import { EmptyState, Tabs } from "../components/molecules";
import { ExpenseForm, ExpenseList, GroupHeader, MembersPanel, SettlementPanel } from "../components/organisms";
import { useExpenses } from "../hooks/useExpenses";
import { useGroup } from "../hooks/useGroup";
import { useMembers } from "../hooks/useMembers";
import { useSettlement } from "../hooks/useSettlement";

type TabId = "expenses" | "settlement" | "members";

const TABS: { id: TabId; label: string }[] = [
  { id: "expenses", label: "Despesas" },
  { id: "settlement", label: "Quitação" },
  { id: "members", label: "Integrantes" },
];

export default function GroupPage() {
  const { groupId = "" } = useParams();
  const [activeTab, setActiveTab] = useState<TabId>("expenses");

  const groupQuery = useGroup(groupId);
  const members = useMembers(groupId);
  const expenses = useExpenses(groupId);
  const { settlement, payments, pay } = useSettlement(groupId);

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
  const memberList = members.list.data ?? [];

  return (
    <div className="space-y-6">
      <GroupHeader name={group.name} currency={group.baseCurrency} />
      <Tabs tabs={TABS} active={activeTab} onChange={setActiveTab} />

      {activeTab === "expenses" && (
        <>
          {memberList.length > 0 && (
            <ExpenseForm
              key={memberList.map((m) => m.id).join()}
              members={memberList}
              currency={group.baseCurrency}
              onSubmit={expenses.create.mutateAsync}
              isSubmitting={expenses.create.isPending}
              errorMessage={expenses.create.error?.message}
            />
          )}
          <ExpenseList
            expenses={expenses.list.data}
            currency={group.baseCurrency}
            isLoading={expenses.list.isPending}
          />
        </>
      )}

      {activeTab === "settlement" &&
        (settlement.data ? (
          <SettlementPanel
            settlement={settlement.data}
            expenses={expenses.list.data ?? []}
            payments={payments.data ?? []}
            onMarkAsPaid={(transfer) =>
              pay.mutate({ fromMemberId: transfer.from.id, toMemberId: transfer.to.id, amount: transfer.amount })
            }
            isPaying={pay.isPending}
            errorMessage={pay.error?.message}
          />
        ) : (
          <p className="text-slate-500">Calculando saldos...</p>
        ))}

      {activeTab === "members" && (
        <MembersPanel
          members={members.list.data}
          isLoading={members.list.isPending}
          onAdd={members.add}
          onRemove={members.remove}
          isAdding={members.isAdding}
          isRemoving={members.isRemoving}
          errorMessage={members.error?.message}
        />
      )}
    </div>
  );
}

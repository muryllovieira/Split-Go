import type { Member } from "../../api/types";
import { Alert, Card } from "../atoms";
import { InlineForm, MemberRow } from "../molecules";

type Props = {
  members: Member[] | undefined;
  isLoading: boolean;
  onAdd: (name: string) => Promise<unknown>;
  onRemove: (memberId: string) => void;
  isAdding: boolean;
  isRemoving: boolean;
  errorMessage?: string;
};

export default function MembersPanel({ members, isLoading, onAdd, onRemove, isAdding, isRemoving, errorMessage }: Props) {
  return (
    <Card>
      <h2 className="text-lg font-semibold">Integrantes</h2>

      {isLoading && <p className="mt-3 text-sm text-slate-500">Carregando...</p>}

      {members && (
        <ul className="mt-3 divide-y divide-slate-100">
          {members.map((member) => (
            <MemberRow key={member.id} name={member.name} onRemove={() => onRemove(member.id)} disabled={isRemoving} />
          ))}
        </ul>
      )}

      <div className="mt-4">
        <InlineForm
          placeholder="Nome do novo integrante"
          submitLabel="Adicionar"
          onSubmit={onAdd}
          disabled={isAdding}
          maxLength={60}
        />
      </div>

      {errorMessage && (
        <div className="mt-3">
          <Alert message={errorMessage} />
        </div>
      )}
    </Card>
  );
}

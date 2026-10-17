import { useState, type FormEvent } from "react";
import type { CreateGroupInput } from "../../api/types";
import { Alert, Button, Card, Input, Select } from "../atoms";
import { FormField, RemovableInput } from "../molecules";

const CURRENCIES = [
  { value: "BRL", label: "Real (R$)" },
  { value: "USD", label: "Dólar (US$)" },
  { value: "EUR", label: "Euro (€)" },
  { value: "GBP", label: "Libra (£)" },
  { value: "ARS", label: "Peso argentino" },
];

const MIN_MEMBERS = 2;

type Props = {
  onSubmit: (input: CreateGroupInput) => void;
  isSubmitting: boolean;
  errorMessage?: string;
};

export default function CreateGroupForm({ onSubmit, isSubmitting, errorMessage }: Props) {
  const [name, setName] = useState("");
  const [baseCurrency, setBaseCurrency] = useState("BRL");
  const [members, setMembers] = useState(["", ""]);

  function updateMember(index: number, value: string) {
    setMembers((current) => current.map((m, i) => (i === index ? value : m)));
  }

  function removeMember(index: number) {
    setMembers((current) => current.filter((_, i) => i !== index));
  }

  function handleSubmit(event: FormEvent) {
    event.preventDefault();
    onSubmit({ name, baseCurrency, members: members.map((m) => m.trim()).filter(Boolean) });
  }

  return (
    <Card>
      <form onSubmit={handleSubmit} className="space-y-5">
        <div className="grid gap-4 sm:grid-cols-[1fr_auto]">
          <FormField label="Nome do grupo">
            <Input
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="Ex: Viagem para Buenos Aires"
              maxLength={100}
            />
          </FormField>
          <FormField label="Moeda">
            <Select value={baseCurrency} onChange={(e) => setBaseCurrency(e.target.value)} options={CURRENCIES} />
          </FormField>
        </div>

        <fieldset>
          <legend className="text-sm font-medium text-slate-700">Quem participa?</legend>
          <div className="mt-2 space-y-2">
            {members.map((member, index) => (
              <RemovableInput
                key={index}
                value={member}
                onChange={(value) => updateMember(index, value)}
                placeholder={`Integrante ${index + 1}`}
                maxLength={60}
                onRemove={members.length > MIN_MEMBERS ? () => removeMember(index) : undefined}
              />
            ))}
          </div>
          <Button variant="ghost" onClick={() => setMembers((current) => [...current, ""])} className="mt-2 text-sm">
            + Adicionar integrante
          </Button>
        </fieldset>

        {errorMessage && <Alert message={errorMessage} />}

        <Button type="submit" fullWidth disabled={isSubmitting} className="py-2.5">
          {isSubmitting ? "Criando..." : "Criar grupo"}
        </Button>
      </form>
    </Card>
  );
}

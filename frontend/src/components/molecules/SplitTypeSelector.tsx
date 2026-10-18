import type { SplitType } from "../../api/types";

const OPTIONS: { value: SplitType; label: string; hint: string }[] = [
  { value: "EQUAL", label: "Igual", hint: "Todos pagam o mesmo" },
  { value: "PERCENTAGE", label: "Porcentagem", hint: "Cada um paga uma %" },
  { value: "EXACT", label: "Valor fixo", hint: "Cada um paga um valor" },
];

type Props = {
  value: SplitType;
  onChange: (value: SplitType) => void;
};

export default function SplitTypeSelector({ value, onChange }: Props) {
  return (
    <div className="grid grid-cols-3 gap-2" role="radiogroup" aria-label="Como dividir">
      {OPTIONS.map((option) => (
        <button
          key={option.value}
          type="button"
          role="radio"
          aria-checked={option.value === value}
          onClick={() => onChange(option.value)}
          className={`rounded-lg border px-3 py-2 text-left transition-colors ${
            option.value === value
              ? "border-emerald-500 bg-emerald-50 ring-1 ring-emerald-500"
              : "border-slate-300 bg-white hover:bg-slate-50"
          }`}
        >
          <span className="block text-sm font-semibold">{option.label}</span>
          <span className="block text-xs text-slate-500">{option.hint}</span>
        </button>
      ))}
    </div>
  );
}

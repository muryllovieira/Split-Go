import { Avatar, Checkbox, Input } from "../atoms";

type Props = {
  name: string;
  selected: boolean;
  onToggle: (selected: boolean) => void;
  value: string;
  onValueChange: (value: string) => void;
  valueSuffix?: string;
  showValue: boolean;
};

export default function ParticipantRow({ name, selected, onToggle, value, onValueChange, valueSuffix, showValue }: Props) {
  return (
    <div className="flex items-center justify-between gap-3 py-2">
      <label className="flex flex-1 cursor-pointer items-center gap-3">
        <Checkbox checked={selected} onChange={(e) => onToggle(e.target.checked)} />
        <Avatar name={name} />
        <span className={selected ? "" : "text-slate-400"}>{name}</span>
      </label>
      {showValue && selected && (
        <div className="flex w-36 items-center gap-1">
          <Input
            type="number"
            inputMode="decimal"
            step="0.01"
            min="0"
            value={value}
            onChange={(e) => onValueChange(e.target.value)}
            aria-label={`Valor de ${name}`}
            className="text-right"
          />
          {valueSuffix && <span className="text-sm text-slate-500">{valueSuffix}</span>}
        </div>
      )}
    </div>
  );
}

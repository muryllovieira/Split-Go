import { Button, Input } from "../atoms";

type Props = {
  value: string;
  onChange: (value: string) => void;
  placeholder: string;
  onRemove?: () => void;
  maxLength?: number;
};

export default function RemovableInput({ value, onChange, placeholder, onRemove, maxLength }: Props) {
  return (
    <div className="flex gap-2">
      <Input value={value} onChange={(e) => onChange(e.target.value)} placeholder={placeholder} maxLength={maxLength} />
      {onRemove && (
        <Button variant="secondary" onClick={onRemove} aria-label={`Remover ${placeholder}`} className="border-0">
          ✕
        </Button>
      )}
    </div>
  );
}

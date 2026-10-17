import { useState, type FormEvent } from "react";
import { Button, Input } from "../atoms";

type Props = {
  placeholder: string;
  submitLabel: string;
  onSubmit: (value: string) => Promise<unknown>;
  disabled?: boolean;
  maxLength?: number;
};

export default function InlineForm({
  placeholder,
  submitLabel,
  onSubmit,
  disabled = false,
  maxLength,
}: Props) {
  const [value, setValue] = useState("");

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!value.trim()) {
      return;
    }
    try {
      await onSubmit(value.trim());
      setValue("");
    } catch {}
  }

  return (
    <form onSubmit={handleSubmit} className="flex gap-2">
      <Input
        value={value}
        onChange={(e) => setValue(e.target.value)}
        placeholder={placeholder}
        maxLength={maxLength}
      />
      <Button
        type="submit"
        disabled={disabled || !value.trim()}
        className="shrink-0"
      >
        {submitLabel}
      </Button>
    </form>
  );
}

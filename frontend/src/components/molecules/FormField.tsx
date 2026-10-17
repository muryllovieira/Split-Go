import type { ReactNode } from "react";

type Props = {
  label: string;
  children: ReactNode;
};

export default function FormField({ label, children }: Props) {
  return (
    <label className="block">
      <span className="text-sm font-medium text-slate-700">{label}</span>
      <div className="mt-1">{children}</div>
    </label>
  );
}

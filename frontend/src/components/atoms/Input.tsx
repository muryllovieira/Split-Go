import type { InputHTMLAttributes } from "react";

type Props = InputHTMLAttributes<HTMLInputElement>;

export default function Input({ className = "", ...props }: Props) {
  return (
    <input
      className={`w-full rounded-lg border border-slate-300 px-3 py-2 focus:border-emerald-500 focus:outline-none ${className}`}
      {...props}
    />
  );
}

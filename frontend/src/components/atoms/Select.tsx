import type { SelectHTMLAttributes } from "react";

type Option = {
  value: string;
  label: string;
};

type Props = SelectHTMLAttributes<HTMLSelectElement> & {
  options: Option[];
};

export default function Select({ options, className = "", ...props }: Props) {
  return (
    <select
      className={`w-full rounded-lg border border-slate-300 bg-white px-3 py-2 focus:border-emerald-500 focus:outline-none ${className}`}
      {...props}
    >
      {options.map((option) => (
        <option key={option.value} value={option.value}>
          {option.label}
        </option>
      ))}
    </select>
  );
}

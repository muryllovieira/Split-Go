import type { InputHTMLAttributes } from "react";

type Props = Omit<InputHTMLAttributes<HTMLInputElement>, "type">;

export default function Checkbox({ className = "", ...props }: Props) {
  return <input type="checkbox" className={`h-4 w-4 accent-emerald-600 ${className}`} {...props} />;
}

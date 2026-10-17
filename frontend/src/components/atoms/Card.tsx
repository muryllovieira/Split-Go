import type { HTMLAttributes } from "react";

type Props = HTMLAttributes<HTMLElement>;

export default function Card({ className = "", ...props }: Props) {
  return <section className={`rounded-xl border border-slate-200 bg-white p-6 shadow-sm ${className}`} {...props} />;
}

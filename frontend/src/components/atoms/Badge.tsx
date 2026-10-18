import type { ReactNode } from "react";

type Props = {
  children: ReactNode;
};

export default function Badge({ children }: Props) {
  return (
    <span className="inline-flex items-center rounded-full bg-slate-100 px-2 py-0.5 text-xs font-medium text-slate-600">
      {children}
    </span>
  );
}

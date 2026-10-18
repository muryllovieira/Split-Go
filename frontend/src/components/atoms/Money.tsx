import { formatMoney } from "../../lib/format";

type Props = {
  value: number;
  currency: string;
  signed?: boolean;
  className?: string;
};

export default function Money({ value, currency, signed = false, className = "" }: Props) {
  const color = !signed ? "" : value > 0 ? "text-emerald-600" : value < 0 ? "text-red-600" : "text-slate-500";
  const prefix = signed && value > 0 ? "+" : "";
  return (
    <span className={`tabular-nums ${color} ${className}`}>
      {prefix}
      {formatMoney(value, currency)}
    </span>
  );
}

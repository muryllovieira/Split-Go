import type { ButtonHTMLAttributes } from "react";

type Variant = "primary" | "secondary" | "ghost" | "danger";

type Props = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: Variant;
  fullWidth?: boolean;
};

const VARIANTS: Record<Variant, string> = {
  primary: "bg-emerald-600 font-semibold text-white hover:bg-emerald-700",
  secondary: "border border-slate-300 bg-white font-medium text-slate-700 hover:bg-slate-50",
  ghost: "font-medium text-emerald-600 hover:underline",
  danger: "text-sm text-slate-400 hover:text-red-600",
};

export default function Button({ variant = "primary", fullWidth = false, className = "", type = "button", ...props }: Props) {
  const padding = variant === "ghost" || variant === "danger" ? "" : "px-4 py-2";
  return (
    <button
      type={type}
      className={`rounded-lg transition-colors disabled:cursor-not-allowed disabled:opacity-60 ${padding} ${VARIANTS[variant]} ${fullWidth ? "w-full" : ""} ${className}`}
      {...props}
    />
  );
}

import { Link, type LinkProps } from "react-router";

export default function TextLink({ className = "", ...props }: LinkProps) {
  return <Link className={`font-medium text-emerald-600 hover:underline ${className}`} {...props} />;
}

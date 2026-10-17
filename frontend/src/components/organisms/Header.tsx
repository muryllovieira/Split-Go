import { Link } from "react-router";

export default function Header() {
  return (
    <header className="border-b border-slate-200 bg-white">
      <div className="mx-auto flex max-w-3xl items-center px-4 py-3">
        <Link to="/" className="text-lg font-bold tracking-tight text-emerald-600">
          Split<span className="text-slate-900">&amp;</span>Go
        </Link>
      </div>
    </header>
  );
}

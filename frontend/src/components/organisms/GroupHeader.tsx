import { CopyLinkButton } from "../molecules";

type Props = {
  name: string;
  currency: string;
};

export default function GroupHeader({ name, currency }: Props) {
  return (
    <div className="flex flex-wrap items-start justify-between gap-4">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">{name}</h1>
        <p className="mt-1 text-sm text-slate-500">Valores em {currency}</p>
      </div>
      <CopyLinkButton />
    </div>
  );
}

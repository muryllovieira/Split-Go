type Props = {
  name: string;
};

export default function Avatar({ name }: Props) {
  return (
    <span
      aria-hidden="true"
      className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-emerald-100 text-sm font-semibold text-emerald-700"
    >
      {name.charAt(0).toUpperCase()}
    </span>
  );
}

import { TextLink } from "../atoms";

type Props = {
  title: string;
  description: string;
  actionLabel: string;
  actionTo: string;
};

export default function EmptyState({ title, description, actionLabel, actionTo }: Props) {
  return (
    <div className="py-16 text-center">
      <h1 className="text-2xl font-semibold">{title}</h1>
      <p className="mt-2 text-slate-500">{description}</p>
      <TextLink to={actionTo} className="mt-6 inline-block">
        {actionLabel}
      </TextLink>
    </div>
  );
}

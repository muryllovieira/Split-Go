import { Avatar, Button } from "../atoms";

type Props = {
  name: string;
  onRemove: () => void;
  disabled?: boolean;
};

export default function MemberRow({ name, onRemove, disabled = false }: Props) {
  return (
    <li className="flex items-center justify-between py-2">
      <span className="flex items-center gap-3">
        <Avatar name={name} />
        {name}
      </span>
      <Button variant="danger" onClick={onRemove} disabled={disabled}>
        Remover
      </Button>
    </li>
  );
}

import type { Transfer } from "../../api/types";
import { Avatar, Button, Money } from "../atoms";

type Props = {
  transfer: Transfer;
  currency: string;
  onMarkAsPaid: () => void;
  disabled: boolean;
};

export default function TransferItem({ transfer, currency, onMarkAsPaid, disabled }: Props) {
  return (
    <li className="flex flex-wrap items-center justify-between gap-3 py-3">
      <div className="flex items-center gap-2">
        <Avatar name={transfer.from.name} />
        <span>
          <strong>{transfer.from.name}</strong> paga{" "}
          <Money value={transfer.amount} currency={currency} className="font-semibold" /> para{" "}
          <strong>{transfer.to.name}</strong>
        </span>
      </div>
      <Button variant="secondary" onClick={onMarkAsPaid} disabled={disabled} className="text-sm">
        ✓ Marcar como pago
      </Button>
    </li>
  );
}

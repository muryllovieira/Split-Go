import { useState } from "react";
import { Button } from "../atoms";

export default function CopyLinkButton() {
  const [copied, setCopied] = useState(false);

  async function copyLink() {
    await navigator.clipboard.writeText(window.location.href);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  }

  return (
    <Button variant="secondary" onClick={copyLink} className="text-sm">
      {copied ? "✓ Link copiado!" : "🔗 Copiar link do grupo"}
    </Button>
  );
}

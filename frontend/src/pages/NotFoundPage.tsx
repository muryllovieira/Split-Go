import { EmptyState } from "../components/molecules";

export default function NotFoundPage() {
  return (
    <EmptyState
      title="Página não encontrada"
      description="Confira o link ou crie um novo grupo."
      actionLabel="Voltar ao início"
      actionTo="/"
    />
  );
}

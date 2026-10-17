import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { createBrowserRouter, RouterProvider } from "react-router";
import "./index.css";
import { MainTemplate } from "./components/templates";
import HomePage from "./pages/HomePage";
import GroupPage from "./pages/GroupPage";
import NotFoundPage from "./pages/NotFoundPage";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: { retry: false, refetchOnWindowFocus: false },
  },
});

const router = createBrowserRouter([
  {
    element: <MainTemplate />,
    children: [
      { path: "/", element: <HomePage /> },
      { path: "/groups/:groupId", element: <GroupPage /> },
      { path: "*", element: <NotFoundPage /> },
    ],
  },
]);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
    </QueryClientProvider>
  </StrictMode>,
);

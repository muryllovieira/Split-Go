export type Group = {
  id: string;
  name: string;
  baseCurrency: string;
  createdAt: string;
};

export type Member = {
  id: string;
  name: string;
  createdAt: string;
};

export type CreateGroupInput = {
  name: string;
  baseCurrency: string;
  members: string[];
};

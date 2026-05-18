const COLORS = ['#6366f1', '#f59e0b', '#10b981', '#f97316', '#8b5cf6', '#06b6d4'];

export function accentColor(id: number): string {
  return COLORS[id % COLORS.length];
}

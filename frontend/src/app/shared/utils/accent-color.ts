const COLORS = ['#6366f1', '#f59e0b', '#10b981', '#ef4444', '#8b5cf6', '#06b6d4'];

export function accentColor(id: number): string {
  return COLORS[id % COLORS.length];
}

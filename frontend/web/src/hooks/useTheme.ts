import { useState, useEffect } from 'react';

type Theme = 'light' | 'dark' | 'auto';

const THEME_STORAGE_KEY = 'app_theme';

export const useTheme = () => {
  const [theme, setThemeState] = useState<Theme>('light');
  const [isDark, setIsDark] = useState(false);

  // 初始化主题
  useEffect(() => {
    const savedTheme = localStorage.getItem(THEME_STORAGE_KEY) as Theme;
    const initialTheme = savedTheme || 'light';
    setThemeState(initialTheme);
    applyTheme(initialTheme);
  }, []);

  const applyTheme = (newTheme: Theme) => {
    let shouldBeDark = false;

    if (newTheme === 'dark') {
      shouldBeDark = true;
    } else if (newTheme === 'light') {
      shouldBeDark = false;
    } else {
      // 自动模式：跟随系统
      shouldBeDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    }

    setIsDark(shouldBeDark);
    document.documentElement.setAttribute('data-theme', shouldBeDark ? 'dark' : 'light');
  };

  const setTheme = (newTheme: Theme) => {
    setThemeState(newTheme);
    localStorage.setItem(THEME_STORAGE_KEY, newTheme);
    applyTheme(newTheme);
  };

  const toggleTheme = () => {
    const newTheme: Theme = theme === 'light' ? 'dark' : 'light';
    setTheme(newTheme);
  };

  return {
    theme,
    isDark,
    setTheme,
    toggleTheme,
  };
};

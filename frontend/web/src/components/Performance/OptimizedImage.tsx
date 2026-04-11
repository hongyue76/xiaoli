import React, { useState, useRef, useEffect } from 'react';
import { Image } from 'antd';
import './OptimizedImage.css';

interface OptimizedImageProps {
  src: string;
  alt: string;
  width?: number | string;
  height?: number | string;
  className?: string;
  cdnUrl?: string;
  fallback?: string;
  placeholder?: string;
  lazy?: boolean;
  preview?: boolean;
  webP?: boolean;
  onError?: () => void;
  onLoad?: () => void;
}

/**
 * 图片优化组件
 * 功能：
 * 1. WebP 格式支持（优先使用 WebP 格式，回退到原格式）
 * 2. 懒加载（使用 Intersection Observer API）
 * 3. CDN 加速（可选添加 CDN 前缀）
 * 4. 渐进式加载（placeholder 模糊效果）
 * 5. 错误处理和回退图片
 */
const OptimizedImage: React.FC<OptimizedImageProps> = ({
  src,
  alt,
  width = '100%',
  height = 'auto',
  className = '',
  cdnUrl,
  fallback,
  placeholder,
  lazy = true,
  preview = false,
  webP = true,
  onError,
  onLoad,
}) => {
  const [isLoaded, setIsLoaded] = useState(false);
  const [hasError, setHasError] = useState(false);
  const [imageSrc, setImageSrc] = useState('');
  const imgRef = useRef<HTMLImageElement>(null);
  const observerRef = useRef<IntersectionObserver | null>(null);

  // 构建 CDN URL
  const buildCDNUrl = (url: string): string => {
    if (!cdnUrl) return url;
    // 移除 URL 中的协议部分，统一使用 CDN
    const cleanUrl = url.replace(/^https?:\/\//, '');
    return `${cdnUrl}/${cleanUrl}`;
  };

  // 检测 WebP 支持
  const checkWebPSupport = (): boolean => {
    if (typeof window === 'undefined') return false;
    const canvas = document.createElement('canvas');
    canvas.width = 1;
    canvas.height = 1;
    return canvas.toDataURL('image/webp').indexOf('data:image/webp') === 0;
  };

  // 构建 WebP URL
  const buildWebPUrl = (url: string): string => {
    const webPUrl = url.replace(/\.(jpg|jpeg|png)$/i, '.webp');
    return webPUrl;
  };

  // 初始化图片源
  useEffect(() => {
    let finalSrc = src;

    // 应用 CDN
    if (cdnUrl) {
      finalSrc = buildCDNUrl(finalSrc);
    }

    // 应用 WebP
    if (webP && checkWebPSupport()) {
      finalSrc = buildWebPUrl(finalSrc);
    }

    setImageSrc(finalSrc);
  }, [src, cdnUrl, webP]);

  // 设置 Intersection Observer 实现懒加载
  useEffect(() => {
    if (!lazy) {
      setIsLoaded(true);
      return;
    }

    const imgElement = imgRef.current;
    if (!imgElement) return;

    observerRef.current = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            setIsLoaded(true);
            if (observerRef.current) {
              observerRef.current.unobserve(imgElement);
            }
          }
        });
      },
      {
        rootMargin: '50px 0px',
        threshold: 0.01,
      }
    );

    observerRef.current.observe(imgElement);

    return () => {
      if (observerRef.current) {
        observerRef.current.disconnect();
      }
    };
  }, [lazy]);

  // 处理加载错误
  const handleError = () => {
    setHasError(true);
    if (onError) onError();

    // 如果是 WebP 错误，尝试回退到原格式
    if (webP && imageSrc.endsWith('.webp')) {
      const originalUrl = src.replace(/\.webp$/i, '');
      setImageSrc(cdnUrl ? buildCDNUrl(originalUrl) : originalUrl);
      setHasError(false);
    }
  };

  // 处理加载完成
  const handleLoad = () => {
    if (onLoad) onLoad();
  };

  // 渲染错误状态
  if (hasError && fallback) {
    return (
      <img
        src={fallback}
        alt={alt}
        width={width}
        height={height}
        className={`optimized-image ${className}`}
      />
    );
  }

  // 使用 Ant Design Image 组件（带预览功能）或原生 img
  if (preview) {
    return (
      <Image
        src={isLoaded || !lazy ? imageSrc : placeholder || ''}
        alt={alt}
        width={width}
        height={height}
        className={`optimized-image ${className}`}
        preview={preview}
        onError={handleError}
        onLoad={handleLoad}
        fallback={fallback}
        style={{
          opacity: isLoaded || !lazy ? 1 : 0,
          transition: 'opacity 0.3s ease-in-out',
        }}
      />
    );
  }

  return (
    <div className="optimized-image-wrapper" style={{ width, height }}>
      {/* 占位符 */}
      {placeholder && !isLoaded && lazy && (
        <img
          src={placeholder}
          alt=""
          className="optimized-image placeholder"
          style={{
            position: 'absolute',
            top: 0,
            left: 0,
            width: '100%',
            height: '100%',
            objectFit: 'cover',
            filter: 'blur(10px)',
          }}
        />
      )}

      {/* 实际图片 */}
      <img
        ref={imgRef}
        src={isLoaded || !lazy ? imageSrc : ''}
        alt={alt}
        className={`optimized-image ${className}`}
        width={width}
        height={height}
        onError={handleError}
        onLoad={handleLoad}
        style={{
          opacity: isLoaded || !lazy ? 1 : 0,
          transition: 'opacity 0.3s ease-in-out',
        }}
        loading={lazy ? 'lazy' : 'eager'}
      />
    </div>
  );
};

export default OptimizedImage;

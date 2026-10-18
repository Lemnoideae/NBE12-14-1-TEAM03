This is a [Next.js](https://nextjs.org) project bootstrapped with [`create-next-app`](https://nextjs.org/docs/app/api-reference/cli/create-next-app).

## Getting Started

First, run the development server:

```bash
npm run dev
# or
yarn dev
# or
pnpm dev
# or
bun dev
```

Open [http://localhost:3000](http://localhost:3000) with your browser to see the result.

## Production Build & Run

프로덕션 빌드와 기동은 다음 순서로 실행합니다:

```bash
# 1. 프로덕션 빌드
npm run build

# 2. 타입 검사 (선택 사항)
npx --no-install tsc --noEmit

# 3. 프로덕션 서버 실행
npm run start
```

### 백엔드 연동 및 접속 Origin 안내

- **백엔드 필요 조건**: 상품 조회, 주문 생성, 관리자 조회 등 주요 기능은 백엔드 서버(`http://localhost:8080`)가 실행되어 있어야 정상 동작합니다.
- **접속 Origin**: 브라우저에서는 반드시 `http://localhost:3000`으로 접속해야 합니다. 백엔드의 CORS 정책상 `http://127.0.0.1:3000`에서 요청 시 주문 `POST /api/orders`가 403 Forbidden으로 거부됩니다.

## Verification

로컬 변경 검증 시 다음 명령을 수행합니다:

```bash
npm run lint                  # 린트 검사
npx --no-install tsc --noEmit # TypeScript 컴파일 검사
npm run build                 # 프로덕션 빌드 검사
```

You can start editing the page by modifying `app/page.tsx`. The page auto-updates as you edit the file.

This project uses [`next/font`](https://nextjs.org/docs/app/building-your-application/optimizing/fonts) to automatically optimize and load [Geist](https://vercel.com/font), a new font family for Vercel.

## Learn More

To learn more about Next.js, take a look at the following resources:

- [Next.js Documentation](https://nextjs.org/docs) - learn about Next.js features and API.
- [Learn Next.js](https://nextjs.org/learn) - an interactive Next.js tutorial.

You can check out [the Next.js GitHub repository](https://github.com/vercel/next.js) - your feedback and contributions are welcome!

## Deploy on Vercel

The easiest way to deploy your Next.js app is to use the [Vercel Platform](https://vercel.com/new?utm_medium=default-template&filter=next.js&utm_source=create-next-app&utm_campaign=create-next-app-readme) from the creators of Next.js.

Check out our [Next.js deployment documentation](https://nextjs.org/docs/app/building-your-application/deploying) for more details.

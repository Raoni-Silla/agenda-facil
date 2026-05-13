# 📅 Agenda Fácil — V1 Finalizada

O **Agenda Fácil** é uma plataforma robusta de gestão de agendamentos para barbearias e salões de beleza. O projeto foi concebido para explorar o que há de mais moderno no desenvolvimento Full-stack, priorizando reatividade, imutabilidade e performance.

---

## 🚀 Destaques Técnicos

### **Front-end (Angular 21 & Reatividade)**
*   **State Management com Signals:** Implementação completa utilizando Signals para um gerenciamento de estado granular e eficiente, reduzindo ciclos de detecção de mudanças desnecessários.
*   **Reatividade com RxJS:** Uso estratégico de `Subject` e operadores como `debounceTime` para buscas globais em tempo real.
*   **Calendário Dinâmico:** Integração com `angular-calendar` e `date-fns`, permitindo visualização mensal com pílulas de contagem de agendamentos.
*   **Modern Control Flow:** Utilização das novas diretivas `@for`, `@if` e `@empty` do Angular para templates mais limpos.
*   **UX & Acessibilidade:** Máscaras de entrada com `ngx-mask` e internacionalização (`pt-BR`) nativa.

### **Back-end (Spring Boot 3.5 & Java 21)**
*   **Java 21 Native:** Aproveitamento de recursos modernos da linguagem, incluindo o uso extensivo de `Records` para DTOs, garantindo imutabilidade.
*   **Spring Data JPA & H2:** Persistência de dados otimizada com suporte a paginação nativa e queries dinâmicas.
*   **Validation:** Regras de negócio rigorosas via Bean Validation (JSR 380) para garantir a integridade dos dados desde o Controller.
*   **Produtividade:** Uso de Lombok para redução de código boilerplate e Spring DevTools para ciclos rápidos de desenvolvimento.

---

## 🛠️ Tecnologias Utilizadas

### **Front-end**
*   **Framework:** Angular 21.2
*   **Estilização:** CSS3 Moderno (Foco em Flexbox/Grid e Responsividade)
*   **UI Components:** Angular Material & CDK
*   **Testes:** Vitest & JSDOM (Substituindo o Karma para maior velocidade)

### **Back-end**
*   **Framework:** Spring Boot 3.5.13
*   **Linguagem:** Java 21
*   **Banco de Dados:** H2 Database (In-memory para desenvolvimento ágil)
*   **Gerenciador de Dependências:** Maven

---

## 📂 Estrutura do Projeto

O repositório está organizado como um monorepo simples:

```bash
/
├── backend/    # API RESTful com Spring Boot
└── frontend/   # Interface SPA com Angular
```

---

## ⚙️ Como Executar

### **Pré-requisitos**
*   Java 21 instalado.
*   Node.js (LTS) e npm instalado.
*   Angular CLI instalado (`npm install -g @angular/cli`).

### **1. Clonar o repositório**
```bash
git clone [https://github.com/raoni-silla/agenda-facil.git](https://github.com/raoni-silla/agenda-facil.git)
cd agenda-facil
```

### **2. Rodar o Back-end**
```bash
cd back
mvn spring-boot:run
```
> A API estará disponível em `http://localhost:8080`. O console do H2 pode ser acessado em `/h2-console`.

### **3. Rodar o Front-end**
```bash
cd front
npm install
npm start
```
> O dashboard estará disponível em `http://localhost:4200`.

---

## 👨‍💻 Autor

**Raoní Mendes Silla**

*   Estudante de Análise e Desenvolvimento de Sistemas (FEMA)
*   Intern Back-end Developer no CEPEIN
*   [LinkedIn](https://linkedin.com/in/raoni-silla) | [GitHub](https://github.com/raoni-silla)

---

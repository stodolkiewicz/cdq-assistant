# CDQ Chat Assistant
# Task Description
• Implement an AI Assistant with Java Frameworks and
Provide a chat interface

# Integrate the following knowledge sources:
- Local Vector database pgvector/pgvector:pg17 populated with CDQ
Product information (RAG, convert to vector embeddings, take plain text
from here https://www.cdq.com/products/cdq-fraud-guard)

- Remote free REST Service ("https://restcountries.com/) → write own mcp server

- Local free MCP server (https://mcpservers.org/servers/semdin/mcp-weather)

# Requirements
• Use a local model qwen3:4b with Ollama
• Provide tests

• Provide answers to the following questions
1 What is the capital city of Germany?  
2 What is the temperature currently in Munich?  
3 What is the temperature of the capital of Germany currently?  
4 What do you know about Berlin?  
<your own questions to show off>  
5 What is CDQ Fraud Guard?   

Answers:

**1. What is the capital city of Germany?**

```
The capital city of Germany is Berlin.
```

**2. What is the temperature currently in Munich?**

```
The current temperature in Munich is **17.6°C**.
```

**3. What is the temperature of the capital of Germany currently?**

```
The current temperature in Berlin (the capital of Germany) is **16.3°C**.
```

**4. What do you know about Berlin?**

```
Berlin is the capital of Germany. According to the CDQ knowledge base, CDQ has a German office located in **Friedrichshafen** (not Berlin). The current temperature in Berlin is **16.3°C**.
```

**5. What is CDQ Fraud Guard?**

```
CDQ Fraud Guard is a service designed to manage and verify global payment data, helping
businesses protect against payment fraud and inaccurate bank account information. It works
by cross-referencing payment details against a shared database of verified accounts and
known fraud cases contributed by the CDQ Data Sharing community. Key features include:

- Enhanced security: Real-time fraud alerts and verification against validated accounts
- Operational efficiency: Streamlined onboarding and fraud case management
- Customizable trust scores: Tailored to specific risk tolerance levels
- Community-driven data: Leverages global fraud intelligence from industry partners

This solution has been adopted by leading corporations including BASF, Siemens, Nestlé, and
Bayer to ensure secure, compliant transactions while reducing fraud risks. As noted in
industry case studies, it has helped companies like Clariant significantly accelerate
business partner onboarding—from up to one month to near-instant processing.

Source: CDQ official documentation and verified client implementations
```



• Out of scope:
o No solution for long/short-term memory required

Task requirements
• Provide the source code of the AI Assistant in a public repository of your choice
• Run the AI assistant and provide the answers
• Please provide a README that describes how to run the service and execute
the tests
• Using AI is explicitly allowed; explain how you used AI to fulfill the task
• If you were not able to fulfill a task, then explain why

## How I used AI

I used Claude Code as a pair-programmer throughout the task — implementation, refactors,
tests, debugging, and this README — working through it interactively rather than accepting
a first draft. Project conventions (SOLID, code style, testing rules) were set once in the
repo's `CLAUDE.md` so they applied consistently across the whole session. I also gave it an
MCP server for live Spring documentation, so it could check current APIs instead of relying
on training data alone.
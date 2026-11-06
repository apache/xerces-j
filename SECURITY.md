# Security Policy

Apache Xerces Java is a mid-level library.
It is generally not intended for direct use by end users. However, its core
functions do include parsing and validating potentially malicious documents
supplied by untrusted users. It is critical that Xerces does this correctly
according to spec regardless of input.
When faced with hostile input, it should report the problem without 
crashing or failing.
It is safe to provide possibly malicious external input to Xerces unless otherwise specified.

## Reporting Security Issues

If you have personally discovered a security issue in Apache Xerces Java
and can explain it, submit it
by following the 
[Apache reporting guidelines](https://security.apache.org/report-code/).

If some security tool is logging a warning, that is as likely as not to be a bug in the tool. You can file that here after you have investigated
if you are willing and able to vouch that it is a true security issue.
Please do not simply echo the output of tools you do not personally understand.

## What is **NOT** a Security Bug in Xerces-Java

1. Anything in your dependency tree whose source code is not in this repo. You control your classpath. Xerces doesn't. If you don't like what's in the classpath, change it.
2. Properly implementing XML 1.0 according to the specification. This includes entity expansion and loading external entity references and DTDs.
3. Properly implementing DTD or W3C XML Schema validation according to the specification.
4. Arbitrary code execution and sandbox escape. Xerces-J is pure Java. Any bug of this nature is almost certainly a bug in the JDK itself and should be reported to the OpenJDK project. You can report a bug like this to us too, but it's unlikely we'll be able to fix the root cause. 
5. Issues that require direct calls to the Xerces Java API.

## Possible Security Bugs in Xerces

* Any XML document, schema, or other external input to Xerces that causes infinite loops in the parser or exponential performance problems.
* Documents, well-formed or otherwise, that cause unexpected runtime exceptions when fed to Xerces. For example, if you pass null directly to a method via an API call, an exception is expected. However, if you're parsing a non-null document and you get a `NullPointerException` or `IndexOutOfBoundsException`, that is at least a bug, and maybe a security issue.
* Executing arbitrary shell commands or other non-Java code 
* Bugs in XML processing or schema validation. Most of these are likely just bugs we should fix, but in the right context such a bug could have security implications.
* Stack traces shown directly to end-users on the terminal instead of passing through a logging API or an exception chain. This issue is about a 1 on a scale of 1-10, but it's still at least a bug we should address.
* The use of Java object serialization in a way that violates Xerces' normal protections and invariants

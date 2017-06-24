# DNS Spoofing Detection #

This is a simple command line utility to help to help windows users check if their DNS queries are spoofed.
The spoofing can be either via hosts file , DNS cache poisoning, or ISP.

1. Program does a Dummy DNS check
	1. Checks if fake DNS Resolver returns proper IP.
	2. Checks if fake Domain Returns Proper IP.
2. Program does both Internal Check and nslookup check to weed out false positives.
	1. Non existent domain.
	2. Faulty DNS settings.
	3. internet switched off
3. if Internal check and nslookup match declare No hijack else perform whois lookup
4. By comparing the whois lookup we can find if the IP belongs to the same company and ensure there is no Dns Spoofing.
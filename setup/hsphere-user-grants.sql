--
-- Grants required for the H-Sphere database user
--

GRANT USAGE ON SCHEMA public TO dasient;
GRANT SELECT ON public.l_server TO dasient;
GRANT SELECT ON public.l_server_groups TO dasient;
GRANT SELECT ON public.p_server TO dasient;
GRANT SELECT ON public.parent_child TO dasient;
GRANT SELECT ON public.domains TO dasient;
GRANT SELECT ON public.unix_user TO dasient;
GRANT SELECT ON public.l_server_ips TO dasient;
GRANT SELECT ON public.accounts TO dasient;
GRANT SELECT ON public.plan_value TO dasient;

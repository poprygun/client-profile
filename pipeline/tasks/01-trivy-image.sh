#!/usr/bin/env sh

set -u

trivy image ${CI_REGISTRY}/rapidportfoliomod/client-profile:latest

